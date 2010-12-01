(ns world)

(defstruct player :location :items)

(defstruct item :description)

(defstruct container :description :items)

(defstruct room :exits :description :items)


(def *player* :player1)

(def *world* {:rooms {:hall (struct room
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
              :mobiles {:orc {:description "Angry Orc" :location :kitchen :think (fn[world] ((println "Hmmmm") (world)))}}
              :items {:sword (struct item "A very pointy sword.")}})


(def the-world (ref *world*))

            
