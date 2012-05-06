(ns retwis.models.user
  (:require [clj-redis.client :as redis]
            [noir.util.crypt :as crypt]
            [noir.validation :as vali]
            [noir.serssion :as session]))

;; Helpers

(defn me []
  (session/get :username))

(defn random-string 
  "generates a random string of a given length"
  [length]
  (let [ascii-codes (concat (range 48 58) (range 66 91) (range 97 123))]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

;; Checks

(defn valid-user? [username]
  (vali/rule (not (redis/get username))
             [:username "That username is already taken"])
  (vali/rule (vali/min-length? username 3)
             [:username "Username must be at least 3 characters long"])
  (not (vali/errors? :username)))

(defn logged-in? []
  (= (redis/get (str "uid:" (session/get :uid) ":auth"))
     (redis/get (str "auth:" (session/get :auth)))))

;; Operations

(defn login! [{:keys [username password] :as user}]
  (let [uid (redis/get (str "username:" username ":uid"))]
   (if (and (uid) (= password (redis/get (str "uid:" uid ":password"))))
     (let [auth (redis/get (str "uid:" uid ":auth"))]
      (do
        (session/put! :auth auth)
        (session/put! :username username)
        (session/put! :uid uid)
        (redis/set (str "auth:" auth) true)))
     (vali/set-error :password "Invalid username or password"))))

(defn logout! []
  (if (logged-in?)
    (do
      (redis/set (str "uid:" (session/get :uid) ":auth") (random-string 26))
      (session/remove! :auth)
      (session/remove! :username)
      (session/remove! :uid))))

(defn add! [{:keys [username password] :as user}]
  (when (valid-user? username)
    (let [uid (redis/incr "global:nextUserId")]
      (redis/set (str "username:" username ":uid") uid)
      (redis/set (str "uid:" uid ":username") username)
      (redis/set (str "uid:" uid ":password") password)
      (redis/set (str "uid:" uid ":auth") (random-string 26)))))