(ns retwis.views.main
  (:require [retwis.views.common :as common]
            [retwis.models.user :as user]
            [noir.response :as resp])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/" []
         (common/layout
           (if (user/logged-in?)
             [:p "Hello " (user/me) "!" [:a {:href "/logout"} "logout"]]
             [:p "You should " [:a {:href "/login"} "login"]])))

(defpage "/login" []
         (common/layout
           (common/login)))

(defpage [:post "/login"] {:keys [username password] :as user}
         (user/login! user)
         (resp/redirect "/"))

(defpage "/logout" []
         (user/logout!)
         (resp/redirect "/"))