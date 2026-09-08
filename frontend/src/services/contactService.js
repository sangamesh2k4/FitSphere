// contactService.js
import api from "./api";

const contactService = {
  submitMessage: async (data) => {
    const response = await api.post("/contact", data);
    return response.data;
  },
};

export default contactService;