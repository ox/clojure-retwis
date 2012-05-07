(ns retwis.models.user
  (:require [clj-redis.client :as redis]
            [noir.util.crypt :as crypt]
            [noir.validation :as vali]
            [noir.session :as session]))

(def db (redis/init))

;; Helpers

(defn me []
  (session/get :username))

(defn exists? [username]
  (not (nil? (redis/get db (str "username:" username ":uid")))))

(defn random-string 
  "generates a random string of a given length"
  [length]
  (let [ascii-codes (concat (range 48 58) (range 66 91) (range 97 123))]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

;; Checks

(defn valid-user? [username]
  (vali/rule (not (redis/get db username))
             [:username "That username is already taken"])
  (vali/rule (vali/min-length? username 3)
             [:username "Username must be at least 3 characters long"])
  ;; hack
  (not (binding [^:dynamic vali/*errors* (atom {})]
         (vali/errors? :username))))

(defn logged-in? []
  (if (session/get :auth)
    (if (session/get :user)
      true
      (if (not (nil? (redis/get db (str "auth:" (redis/get db (str "uid:" (session/get :uid) ":auth"))))))
        (session/put! :user true)
        false))))

;; Operations

(defn login! [{:keys [username password] :as user}]
  (let [uid (redis/get db (str "username:" username ":uid"))]
   (if (and 
         (not (nil? uid))
         (not (nil? (redis/get db (str "uid:" uid ":password"))))
         (= password (redis/get db (str "uid:" uid ":password"))))
     (let [auth (redis/get db (str "uid:" uid ":auth"))]
      (do
        (session/put! :auth auth)
        (session/put! :username username)
        (session/put! :uid uid)
        (println (session/get :auth) (session/get :username) (session/get :uid))
        (redis/set db (str "auth:" auth) "true")))
     (vali/set-error :password "Invalid username or password"))))

(defn logout! []
  (if (logged-in?)
    (do
      (println "setting " (str "uid:" (session/get :uid) ":auth"))
      (redis/set db (str "uid:" (session/get :uid) ":auth") (random-string 26))
      (session/remove! :auth)
      (session/remove! :username)
      (session/remove! :uid))))

(defn add! [{:keys [username password] :as user}]
  (when (valid-user? username)
    (let [uid (redis/incr db "global:nextUserId")]
      (redis/set db (str "username:" username ":uid") (str uid))
      (redis/set db (str "uid:" uid ":username") username)
      (redis/set db (str "uid:" uid ":password") password)
      (redis/set db (str "uid:" uid ":auth") (random-string 26)))))


(defn init! []
  (add! {:username "admin" :password "admin"})
  (println "adding admin user..."))

