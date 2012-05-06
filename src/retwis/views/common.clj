(ns retwis.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "retwis"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                content]]))

(defpartial login []
            [:form {:action "/login" :method "POST"}
              [:input {:type "text" :name "username"}]
              [:input {:type "password" :name "password"}]
              [:input {:type "submit" :value "login"}]]) 