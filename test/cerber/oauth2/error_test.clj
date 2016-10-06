(ns cerber.oauth2.error-test
  (:require [midje.sweet :refer :all]
            [cerber.oauth2.common :refer :all]
            [cerber.oauth2.authorization :refer [authorize!]]
            [cerber.stores.client :refer [create-client create-client-store with-client-store]]))

(fact "Authorization fails when requested by unknown client."
      (with-client-store (create-client-store :in-memory)
        (let [client (create-client "http://localhost" ["http://localhost"] ["photo"] nil nil false)
              req {:request-method :get
                   :params {:response_type "code"}}]
          (:error (authorize! (assoc-in req [:params :client_id] (:id client)))) => "invalid_request"
          (:error (authorize! (assoc-in req [:params :client_id] "foo"))) => "invalid_client")))

(fact "Authorization fails when requested with unknown scope."
      (with-client-store (create-client-store :in-memory)
        (let [client (create-client "http://localhost" ["http://localhost"] ["photo"] nil nil false)
              req {:request-method :get
                   :params {:response_type "code"
                            :client_id (:id client)}}]
          (:error (authorize! (assoc-in req [:params :scope] "foo"))) => "invalid_scope"
          (:error (authorize! (assoc-in req [:params :scope] "photo"))) => "invalid_request")))