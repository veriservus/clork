(ns clork-test
  (:use [clork.world]
        [clork.game]
        [clojure.test]))

(defn clork-fixture [f]
  (def *test-world* {:rooms {:hall (struct room
                                           {:w :kitchen
                                            :n :study}
                                           "hall"
                                           [:sword])
                             :kitchen (struct room
                                              {:e :hall}
                                              "kitchen")
                             :study (struct room
                                            {:s :hall}
                                            "study")}
                     :players {:player1 (struct player :hall [])
                               :player2 (struct player :kitchen [])}
                     :mobiles { :orc {:description "Angry Orc" :location :kitchen :think (fn [world] (println "Hmmmm") world)}}
                     :items {:sword (struct item "A very pointy sword.")}})
  (f))

(use-fixtures :once clork-fixture)

(deftest desc-exits-test
         (is (= "North West" (desc-exits (get-in *test-world* [:rooms :hall])))))

(deftest move-test
         (is (= :kitchen (get-in (move *test-world* :player1 :w) [:players :player1 :location])))
         (is (= :study (get-in (move *test-world* :player1 :n) [:players :player1 :location])))
         (is (= :hall (get-in (move *test-world* :player1 :e) [:players :player1 :location]))))

(deftest get-room-items-test
         (is (= [:sword] (get-room-items *test-world* :hall)))
         (is (= [] (get-room-items *test-world* :kitchen))))

(deftest add-to-inventory-test
         (is (= 1 (count (get-in (add-to-inventory *test-world* :player1 :sword)  [:players :player1 :items]))))
         (is (= [:sword] (get-in (add-to-inventory *test-world* :player1 :sword) [:players :player1 :items]))))

(deftest remove-item-from-world-test
         (is (empty? (get-in (remove-item-from-world *test-world* :hall :sword) [:rooms :hall :items])))
         )

(deftest pick-up-test
         (is (= :sword (get-in (pick-up *test-world* :player1 :sword) [:players :player1 :items 0])))
         (is (= [] (get-in (pick-up *test-world* :player1 :penguin) [:players :player1 :items])))
         (is (= [] (get-in (pick-up *test-world* :player1 :sword) [:rooms :hall :items]))))

(deftest find-player-test
         (is (= { :location :hall :items []} (find-player *test-world* :player1))))

(deftest only-possible-commands
         (is (= 2 (count (possible-commands)))))

(deftest transfer-items-test
         (let [chest (struct container "Sturdy chest" [])
               adventurer (struct player :hall [:gold])
               [new-player new-chest] (transfer-item adventurer chest :gold)]
           (is (some #{:gold} (:items new-chest)))
           (is (not (some #{:gold} (:items new-player))))))

(deftest transfer-absent-item-test
         (let [chest (struct container "A Strudy chest" [])
               adventurer (struct player :hall [])
               [new-player new-chest] (transfer-item adventurer chest :penguin)]
           (is (= [] (:items new-chest)))))

(deftest transfer-if-item-already-exist
         (let [chest (struct container "A Strudy chest" [:hallbird])
               adventurer (struct player :hall [:gold])
               [new-player new-chest] (transfer-item adventurer chest :gold)]
           (is (= [:hallbird :gold] (:items new-chest)))))

(deftest transfer-if-item-not-exist
         (let [chest (struct container "A Strudy chest" [:hallbird])
               adventurer (struct player :hall [:gold])
               [new-player new-chest] (transfer-item adventurer chest :penguin)]
           (is (= [:hallbird] (:items new-chest)))))


(deftest find-mobile-test
         (is (= :kitchen (find-mobile *test-world* :orc))))

(deftest find-thinkers-test
         (is (= 1 (count (find-thinkers *test-world*)))))

(deftest should-return-the-result-of-invoking-the-function
         (is (= *test-world* ((first (find-thinkers *test-world*)) *test-world*))))

(deftest test-move-help
          (help look))
