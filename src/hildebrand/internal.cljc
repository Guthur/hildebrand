(ns hildebrand.internal
  (:require [eulalie.support :refer [translate-error-type]]
            [eulalie.dynamo]
            [hildebrand.internal.request]
            [hildebrand.internal.response])
  #? (:cljs (:require-macros [hildebrand.internal])))

(def aws-error->hildebrand
  {:resource-not-found-exception :resource-not-found
   :conditional-check-failed-exception :conditional-failed})

(defmethod translate-error-type :eulalie.service/dynamo [_ error-type]
  (aws-error->hildebrand error-type error-type))

(defn restructure-response [target body]
  (let [resp (hildebrand.internal.response/restructure-response target body)]
    (if (and (map? resp) (:hildebrand/error resp))
      (let [{{:keys [type] :as error} :hildebrand/error} resp]
        (ex-info (name type) error))
      resp)))

#? (:clj
    (defmacro defissuer [target-name args & [doc]]
      `(eulalie.support/defissuer :dynamo
         ~target-name ~args
         hildebrand.internal.request/restructure-request
         hildebrand.internal/restructure-response
         ~doc)))
