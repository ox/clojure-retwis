(ns retwis.views.main
  (:use noir.core
        hiccup.core
        hiccup.form-helpers
        hiccup.page-helpers)
  (:require [retwis.views.common :as common]
            [retwis.models.user :as user]
            [noir.response :as resp]
            [noir.validation :as vali]
            [clojure.string :as string]))

;; partials


(defpartial error-text [errors]
            [:div.alert (string/join "" errors)])

(defpartial user-fields [{:keys [username] :as usr}]
            (vali/on-error :username error-text)
            (vali/on-error :password error-text)
            (text-field {:placeholder "Username" :class "span3"} :username username)
            (password-field {:placeholder "Password" :class "span3"} :password))

;; pages

(defpage "/" []
         (common/layout
           (if (user/logged-in?)
             [:a {:href "/logout"} "logout"]
             [:p "You should " [:a {:href "/login"} "login"]])))

;; sessions

(defpage "/login" {:as user}
         (if (user/logged-in?)
           (resp/redirect "/")
           (common/layout
             (form-to {:class "well span3 offset2"} [:post "/login"] 
                      (user-fields user)
                      (submit-button {:class "submit btn"} "submit")))))

(defpage [:post "/login"] {:as user}
         (if (user/login! user)
           (resp/redirect "/")
           (render "/login" user)))

(defpage "/logout" []
         (user/logout!)
         (resp/redirect "/"))