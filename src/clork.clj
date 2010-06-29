(ns clork
  (:use world)
  (:use clojure.contrib.seq-utils))

;; represent a room
;; represent a set of rooms
;; represent monsters

(defstruct player :location :inventory)

(defstruct item :description)

(defstruct container :description :inventory)

(defstruct room :exits :description :items)

(def direction-desc {:n "North" :s "South" :e "East" :w "West"})

(defn desc-exits [room]
  (let [exits (keys (:exits room))
        direction-strings (sort (map #(% direction-desc) exits))]
    (reduce print-str direction-strings)))

(defn look [world player]
  (let [curr-room-name (get-in world [:players player :location])
        curr-room (get-in world [:rooms curr-room-name])
        room-desc (:description curr-room)
        items-in-room (:items curr-room)
        item-descs (map #(get-in world [:items % :description]) items-in-room)]
    (str (println-str room-desc) 
         (println-str "Exits:" (desc-exits curr-room))
         (println-str "Items:" (reduce print-str item-descs)))))

(defn move-player [world player direction]
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
  [world player item] (update-in world [:players player :inventory] #(conj % item))
  )


(defn pick-up [world player item]
  ;; Check that item is actually in the world
  (if (some #{item} (get-room-items world (:location (find-player world player))))
    (add-to-inventory world player item)
    world)
  )

(defn find-player [world player]
  (player (:players world)))
