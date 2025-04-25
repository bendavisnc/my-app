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
       (is (= {:squares [nil, nil, nil, nil, nil, nil, nil, nil, nil]}
              @t))
       (is (s/valid? ::spec/component @t))))))

(deftest new-move-test
  (run-test-sync
   (let [t (re-frame/subscribe [::model/tictactoe])]
     (testing "new move"
       (re-frame/dispatch-sync [:initialize])
       (re-frame/dispatch [::model/on-square-select 0])
       (is (= {:squares [pieces/x, nil, nil, nil, nil, nil, nil, nil, nil]}
              @t))
      ;;  (println (s/explain-str ::spec/component @t))
       (is (s/valid? ::spec/component @t))))))

