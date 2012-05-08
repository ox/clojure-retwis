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
