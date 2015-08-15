(in-ns 'brain-gain-or-drain.screens.game)

(defn move-logo
  [delta-time _ logo]
  (let [delta-x (-> (* 4 60 delta-time)
                    (/ (inc (:z logo))))]
    (assoc logo :x (-> logo :x (- delta-x)))))

(defn move-player
  [delta-time move-direction player]
  (let [delta-y (* 6 60 delta-time)]
    (case move-direction
      :move-up (assoc player :y (-> player :y (+ delta-y)))
      :move-down (assoc player :y (-> player :y (- delta-y)))
      player)))

(defn move-entity
  [delta-time game-input entity]
  (cond (logo? entity) (move-logo delta-time game-input entity)
        (player? entity) (move-player delta-time game-input entity)
        :else entity))

(defn move-entities
  [delta-time game-input entities]
  (mapv #(move-entity delta-time game-input %) entities))
