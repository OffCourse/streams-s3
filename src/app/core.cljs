(ns app.core
  (:require [cljs.nodejs :as nodejs]
            [clojure.walk :as walk]
            [cljs.core.async :refer [<! chan >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn ->js [event]
  (-> event
      js->clj
      walk/keywordize-keys))

(nodejs/enable-util-print!)
(def AWS (nodejs/require "aws-sdk"))
(def dd (nodejs/require "dynamodb-data-types"))
(def doc (nodejs/require "dynamodb-doc"))
(def attr (.-AttributeValue dd))
(def s3 (new AWS.S3))
(def dynamo (.DynamoDB doc))

(defn handle-db-response [response]
  (-> response
      ->js
      :Items))

(defn get-courses []
  (let [c (chan)
        query {:TableName "courses"}]
    (.scan dynamo (clj->js query) #(go (>! c (handle-db-response %2))))
    c))

(defn ->s3 [data]
  (let [c (chan)
        query {:Bucket "dynamo-events"
               :Key "courses.json"
               :Body (.stringify js/JSON (clj->js data))}]
    (.putObject s3 (clj->js query) #(go (>! c (clj->js %2))))
    c))

#_(defn records->courses [records]
  (mapv (fn [record]
          (-> record
              (get-in ["dynamodb" "NewImage"])
              clj->js
              ((.-unwrap attr))
              (js->clj :keywordize-keys true))) records))

#_(defn event->records [event]
  (get-in event ["Records"] event))

(defn ^:export handler [event context cb]
  (go
    (let [courses (<! (get-courses))]
      (cb nil (<! (->s3 courses)))
      (cb "couldn't save event"))))

(defn -main [] identity)
(set! *main-cli-fn* -main)
