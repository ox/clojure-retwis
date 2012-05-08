(ns retwis.views.user
  (:use noir.core
        hiccup.core
        hiccup.form-helpers
        hiccup.page-helpers)
  (:require [retwis.views.common :as common]
            [retwis.models.user :as user]
            [retwis.models.update :as updates]
            [noir.response :as resp]
            [noir.validation :as vali]
            [clojure.string :as string]))

;; Helpers

(defn destructure-update [update]
  (zipmap [:author :time :text] (string/split update #"\|")))


;; partials

(defpartial error-text [errors]
            [:div.alert (string/join "" errors)])

(defpartial post-item [post]
            (when post
              (let [{:keys [author date text] :as update} (destructure-update post)]
              [:li.post
               [:ul.datetime
                 [:li date]]
               [:div.content text]])))

(defpartial update-fields []
            (vali/on-error :text error-text)
            (text-field {:placeholder "What's on your mind?" :class "span4"} :text))


(defpartial post-update-form []
            (form-to {:class "well span4 offset2"} [:post "/home/update"]
                     (update-fields)
                     (submit-button {:class "btn"} "tweet")))

;; main

(pre-route "/home" {}
           (when-not (user/logged-in?)
             (resp/redirect "/login")))

;; view your posts
(defpage "/home" []
         (common/layout
           (post-update-form)
           [:ul.posts
             (map post-item (updates/for-user user/me))]))

(defpage [:post "/home/update"] {:as update}
         (print update)
         (print "HELLO WORLD")
         (updates/add! update)
         (resp/redirect "/home"))