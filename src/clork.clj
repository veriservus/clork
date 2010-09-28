(ns clork
  (:use world)
  (:require [clojure.contrib.seq-utils :as sequ]))

;; represent a room
;; represent a set of rooms
;; represent monsters

(def direction-desc {:n "North" :s "South" :e "East" :w "West"})

(defn desc-exits [room]
  (let [exits (keys (:exits room))
        direction-strings (sort (map #(% direction-desc) exits))]
    (reduce print-str direction-strings)))

(defn find-player [world player]
  (player (:players world)))

(defn look [world player]
  (let [curr-room-name (get-in world [:players player :location])
        curr-room (get-in world [:rooms curr-room-name])
        room-desc (:description curr-room)
        items-in-room (:items curr-room)
        item-descs (map #(get-in world [:items % :description]) items-in-room)]
    (print (str (println-str room-desc) 
         (println-str "Exits:" (desc-exits curr-room))
         (println-str "Items:" (reduce print-str item-descs))))
    world))

(defn move [world player direction]
  (let [curr-room (get-in world [:players player :location])
        routes (get-in world [:rooms curr-room :exits])]
    (if (contains? routes direction)
      (update-in world [:players player] #(merge % {:location (get routes direction)}))
      world)))

(defn get-room-items [world location]
  (let [items (get-in world [:rooms location :items])]
    (if items items [])))

;; All functions should take
(defn add-to-inventory
  [world player item] (update-in world [:players player :items] #(conj % item))
  )
  


(defn remove-item-from-world [world room item]
  (update-in world [:rooms room :items] (fn [coll] (filter #(not= item %) coll)))
  )

(defn pick-up [world player item]
  ;; Check that item is actually in the world
  (let [room (:location (find-player world player))]
    (if (some #{item} (get-room-items world room))
      (-> world
          (add-to-inventory player item)
          (remove-item-from-world room item))
      world)))

(defn transfer-item [giver receiver item]
  ;; make sure we dont transfer non-existent items
  (let [r-items (into (:items receiver) (filter #(= % item) (:items giver)))
        g-items (filter #(not= % item) (:items giver))
        new-receiver (assoc receiver :items r-items)
        new-giver (assoc giver :items g-items)]
    [new-giver new-receiver]))

