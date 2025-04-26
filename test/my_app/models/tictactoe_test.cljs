(ns my-app.models.tictactoe-test
  (:require
   [cljs.test :refer-macros [testing is]]
   [day8.re-frame.test :refer [run-test-sync]]
   [devcards.core :refer-macros [deftest]]
   [my-app.models.tictactoe :as model]
   [my-app.pieces.tictactoe :as pieces]
   [my-app.specs.tictactoe :as spec]
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]))

(deftest initial-state-test
  (run-test-sync
   (let [t (re-frame/subscribe [::model/tictactoe])]
     (testing "initial state"
       (re-frame/dispatch [:initialize])
       (is (= {:squares [nil nil nil nil nil nil nil nil nil],
               :winner nil,
               :history [{:squares [nil nil nil nil nil nil nil nil nil]}],
               :status "Next player: ✘"}
              @t))
       (is (s/valid? ::spec/component @t))))))

(deftest new-move-test
  (run-test-sync
   (let [t (re-frame/subscribe [::model/tictactoe])]
     (testing "new move"
       (re-frame/dispatch-sync [:initialize])
       (re-frame/dispatch [::model/on-square-select 0])
       (is (= "Next player: ⭕"
              (:status @t)))
       (is (s/valid? ::spec/component @t))))))

(deftest winning-move-test
  (run-test-sync
   (let [t (re-frame/subscribe [::model/tictactoe])]
     (testing "winning move"
       (re-frame/dispatch-sync [:initialize])
       (re-frame/dispatch [::model/on-square-select 0])
       (re-frame/dispatch [::model/on-square-select 1])
       (re-frame/dispatch [::model/on-square-select 4])
       (re-frame/dispatch [::model/on-square-select 2])
       (re-frame/dispatch [::model/on-square-select 8])
       (is (= "✘"
              (:winner @t)))
       (is (s/valid? ::spec/component @t))))))

