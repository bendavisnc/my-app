(ns my-app.models.tictactoe-test
  (:require [my-app.models.tictactoe :as model]
            [re-frame.core :as re-frame]
            [day8.re-frame.test :refer [run-test-sync]]
            [cljs.test :refer [use-fixtures] :refer-macros [testing is]]
            [devcards.core :refer-macros [deftest]]
            [clojure.spec.alpha :as s]))

(deftest initial-state-test
  (run-test-sync
   (let [t (re-frame/subscribe [::model/tictactoe])]
     (testing "initial state"
       (re-frame/dispatch [:initialize])
       (is (= {:squares [nil, nil, nil, nil, nil, nil, nil, nil, nil]}
              @t))))))
