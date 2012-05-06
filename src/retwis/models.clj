(ns retwis.models
  (:require [clj-redis.client :as redis]
            [retwis.models.user :as users]
            [retwis.models.update :as updates]))

(defn initialize! []
  (users/init!))