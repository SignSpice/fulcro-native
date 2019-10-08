(ns com.fulcrologic.fulcro-native.expo-application
  (:require
    ["expo" :as expo]
    ["create-react-class" :as crc]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.rendering.keyframe-render :as kr]
    [taoensso.timbre :as log]))

(defonce root-ref (atom nil))
(defonce root-component-ref (atom nil))

(defn render-root [root]
  (try
    (let [first-call? (nil? @root-ref)]
      (reset! root-ref root)

      (if-not first-call?
        (when-let [root @root-component-ref]
          (.forceUpdate ^js root)
          root-component-ref)
        (let [Root
              (crc
                #js {:componentDidMount
                     (fn []
                       (this-as this
                         (reset! root-component-ref this)))
                     :componentWillUnmount
                     (fn []
                       (reset! root-component-ref nil))
                     :render
                     (fn []
                       (let [body @root-ref]
                         (try
                           (if (fn? body)
                             (body)
                             body)
                           (catch :default e
                             (log/error e "Render failed")))))})]
          (expo/registerRootComponent Root))))
    (catch :default e
      (log/error e "Unable to mount/refresh"))))

(defn fulcro-app
  "Identical to com.fulcrologic.fulcro.application/fulcro-app, but modifies a few options
  to ensure initial mount works properly for Native Expo apps."
  [options]
  (app/fulcro-app
    (merge
      {:optimized-render! kr/render!
       :render-root!      render-root}
      options)))
