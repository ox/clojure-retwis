(ns retwis.views.common
  (:use [noir.core :only [defpartial]]
        [noir.validation :as vali]
        [hiccup.page-helpers :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
                [:title "retwis"]
                (include-css "/css/reset.css")
                (include-css "/css/bootstrap.css")
                (include-css "/css/bootstrap-responsive.css")]
              [:body
                [:div.container
                  [:div.row
                    [:div.header.span12
                      [:h1 "Retwis"]]]]
                [:div.container
                  [:div.row
                    [:div.span2 "sidebar"]
                    [:div.span10 content]]]
               (include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js")
               (include-js "/js/bootstrap.min.js")]))

(defpartial login []
            [:form {:action "/login" :method "POST" :class "well span3 offset4"}
              [:label "username"]
              [:input {:type "text" :name "username" :class "span3"}]
              [:label "password"]
              [:input {:type "password" :name "password" :class "span3"}]
              [:br]
              [:input {:type "submit" :value "login" :class "btn"}]]) 