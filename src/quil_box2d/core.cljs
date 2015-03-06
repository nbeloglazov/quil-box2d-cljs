(ns quil-box2d.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(defn vec2 [a b]
  (js/Box2D.b2Vec2. a b))

(defn create-body [world shape]
  (let [bd (doto (js/Box2D.b2BodyDef.)
             (.set_type js/Box2D.b2_dynamicBody)
             (.set_position (vec2 0 0)))]
    (doto (.CreateBody world bd)
      (.CreateFixture shape 5))))

(defn read-body [body]
  (let [pos (.GetPosition body)]
    {:x (.get_x pos)
     :y (.get_y pos)
     :angle (.GetAngle body)}))

(defn reset-bodies [bodies]
  (doall
   (map #(doto %
           (.SetTransform (vec2 (* 25 (- (rand) 0.5))
                                (+ 2 (* (rand) 3)))
                          0)
           (.SetLinearVelocity (vec2 0 0))
           (.SetAwake 1)
           (.SetActive 1))
        bodies)))

(defn box2d-setup []
  )

(defn setup []
  (q/frame-rate 30)
  (let [gravity (vec2 0 -10)
        world (js/Box2D.b2World. gravity)
        bd-ground (js/Box2D.b2BodyDef.)
        ground (.CreateBody world bd-ground)
        shape0 (js/Box2D.b2EdgeShape.)
        shape (js/Box2D.b2PolygonShape.)]
    (.Set shape0 (vec2 -40 -25)
                 (vec2 40 -25))
    (.CreateFixture ground shape0 0)
    (.SetAsBox shape 1 1)
    {:world world
     :bodies (reset-bodies (repeatedly 10 #(create-body world shape)))}))

(defn update-state [state]
  (.Step (:world state) 0.1 2 2)
  state)

(defn draw-state [state]
  (q/background 240)
  (q/translate [(/ (q/width) 2)
                0])
  (q/scale 20)
  (q/rotate q/PI)
  (doseq [{:keys [x y angle]} (map read-body (:bodies state))]
    (q/with-translation [x y]
      (q/with-rotation [angle]
        (q/rect 0 0 1 1)))))


(q/defsketch quil-box2d
  :host "quil-box2d"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
