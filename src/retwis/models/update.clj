(ns retwis.models.update
  (:require [clj-redis.client :as redis]
          [noir.util.crypt :as crypt]
          [noir.validation :as vali]
          [noir.session :as session]))

(def db (redis/init))

;; Helpers

;; Operations

(defn add! [body]
  (when (session/get :auth)
    (let [uid (redis/incr db "global:nextPostId") post (str "post:" uid (str (session/get :username) "|" (str (java.util.Date.)) "|" body))]
      (redis/set db post)
      (redis/lpush db (str "uid:" (session/get :uid) ":posts") uid)
      (apply 
        (fn [id] (redis/lpush db (str "uid:" id ":posts") post))
        (redis/get db (str "uid:" (session/get :uid) ":followers"))))))
