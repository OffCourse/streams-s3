(ns app.core
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [<! chan >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(nodejs/enable-util-print!)
(def AWS (nodejs/require "aws-sdk"))
(def dd (nodejs/require "dynamodb-data-types"))
(def attr (.-AttributeValue dd))
(def s3 (new AWS.S3))

(defn ->s3 [data]
  (let [c (chan)
        query {:Bucket "dynamo-events"
               :Key "course-event.json"
               :Body (.stringify js/JSON (clj->js data))}]
    (.putObject s3 (clj->js query) #(go (>! c (clj->js %2))))
    c))

(defn records->courses [records]
  (mapv (fn [record]
          (-> record
              (get-in ["dynamodb" "NewImage"])
              clj->js
              ((.-unwrap attr))
              (js->clj :keywordize-keys true))) records))

(defn event->records [event]
  (get-in event ["Records"] event))

(defn handler [event context cb]
  (let [courses (some-> (js->clj event)
                        (event->records)
                        (records->courses))]
    (go
      (cb nil (<! (->s3 checkpoints)))
      (cb "couldn't save event"))))
