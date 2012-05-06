(ns retwis.server
  (:require [noir.server :as server]
            [retwis.models :as models]))

(server/load-views "src/retwis/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (models/initialize!)
    (server/start port {:mode mode
                        :ns 'retwis})))

