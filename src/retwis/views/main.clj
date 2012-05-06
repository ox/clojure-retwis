(ns retwis.views.main
  (:require [retwis.views.common :as common]
            [retwis.models.user :as user]
            [noir.response :as resp])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/" []
         (common/layout
           [:p "Hello " (user/me) "!"]))

(defpage "/login" []
         (common/layout
           (common/login)))

(defpage [:post "/login"] {:keys [username password] :as user}
         (user/login! user)
         (resp/redirect "/"))
         