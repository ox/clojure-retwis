(ns retwis.models.update
  (:require [clj-redis.client :as redis]
          [noir.util.crypt :as crypt]
          [noir.validation :as vali]
          [noir.session :as session]))

(def db (redis/init))

;; Helpers

;; get posts for a particular user (timeline)
(defn for-user [username]
  (let [uid (redis/get db (str "username:" username ":uid"))]
    (redis/get db (str "uid:" uid ":posts"))))

;; Operations

(defn add! [body]
  (when (session/get :auth)
    (let [uid (redis/incr db "global:nextPostId") post (str "post:" uid (str (session/get :username) "|" (str (java.util.Date.)) "|" body))]
      (redis/lpush db (str "uid:" (session/get :uid) ":posts") (str uid))
      (apply 
        (fn [id] (redis/lpush db (str "uid:" (str id) ":posts") post))
        (redis/get db (str "uid:" (session/get :uid) ":followers"))))))
