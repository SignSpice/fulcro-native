(ns com.fulcrologic.fulcro-native.events)

(defn event-text
  "Returns the text value produced by a TextInput onChange event."
  [text-event]
  #?(:cljs (some-> ^js text-event .-nativeEvent .-text)))
