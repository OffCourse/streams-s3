(set-env!
 :resource-paths #{"src"}
 :dependencies '[[adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                 [adzerk/boot-cljs-repl       "0.3.0"          :scope "test"]
                 [adzerk/boot-reload          "0.4.5"          :scope "test"]
                 [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/clojure         "1.7.0"]
                 [org.clojure/clojurescript   "1.7.228"]
                 [com.cemerick/piggieback     "0.2.1"          :scope "test"]
                 [weasel                      "0.7.0"          :scope "test"]
                 [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload    :refer [reload]]
  '[crisptrutski.boot-cljs-test  :refer [test-cljs]])

(deftask auto-test []
  (merge-env! :resource-paths #{"test"})
  (comp (watch)
        (speak)
        (test-cljs)))


(deftask build []
  (task-options! target {:dir #{"target"}}
                 cljs   {:optimizations :none
                         :compiler-options {:target :nodejs}
                         :source-map true})
  (comp (cljs)
        (target)))

(deftask dev []
  (comp (watch)
        (build)))

