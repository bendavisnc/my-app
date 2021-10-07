(ns my-app.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]))

(defn neat-component [text]
  [:div {:id "neat-id"} text])

(defn mount-root []
  (rdom/render [neat-component "neat"] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
