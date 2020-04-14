import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    data: {}
  },
  mutations: {
    setData(state, value) {
      state.data = value;
    }
  },
  actions: {

  },
  plugins: [createPersistedState()],
})
