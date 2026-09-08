import { useState } from "react";
import contactService from "../services/contactService";
import CustomSelect from "../components/common/CustomSelect"; // Adjust this import path as needed
import "../css/ContactUs.css";

const reasons = [
  {
    value: "ACCOUNT_SUSPENDED",
    label: "Account Suspended",
  },
  {
    value: "ACCOUNT_ISSUE",
    label: "Account Issue",
  },
  {
    value: "BUG_REPORT",
    label: "Bug Report",
  },
  {
    value: "TECHNICAL_PROBLEM",
    label: "Technical Problem",
  },
  {
    value: "OTHER",
    label: "Other",
  },
];

const ContactUs = () => {
  const [formData, setFormData] = useState({
    email: "",
    reason: "",
    message: "",
  });

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setError("");
    setSuccess("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");

    if (!formData.email.trim()) {
      setError("Email is required");
      return;
    }

    if (!formData.reason) {
      setError("Please select a reason");
      return;
    }

    if (
      formData.reason === "OTHER" &&
      !formData.message.trim()
    ) {
      setError("Please describe your issue");
      return;
    }

    try {
      setLoading(true);

      const data = {
        email: formData.email.trim(),
        reason: formData.reason,
        message: formData.message.trim(),
      };

      const response = await contactService.submitMessage(data);

      setSuccess(response || "Message sent successfully");

      setFormData({
        email: "",
        reason: "",
        message: "",
      });
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.response?.data ||
        "Failed to send your message. Please try again.";

      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="contact-page">
      <div className="contact-container">

        <div className="contact-header">
          <span className="contact-eyebrow">GET IN TOUCH</span>

          <h1>Contact Us</h1>

          <p>
            Have an issue with FitSphere? Let us know and we'll
            get back to you as soon as possible.
          </p>
        </div>

        <div className="contact-card">

          <form onSubmit={handleSubmit}>

            <div className="contact-field">
              <label htmlFor="email">
                Email
              </label>

              <input
                id="email"
                name="email"
                type="email"
                placeholder="Enter your email"
                value={formData.email}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            <div className="contact-field">
              <label htmlFor="reason">
                Reason
              </label>

              {/* Replaced standard <select> with CustomSelect */}
              <CustomSelect
                name="reason"
                value={formData.reason}
                onChange={handleChange}
                options={reasons}
                placeholder="Select a reason"
              />
            </div>

            <div className="contact-field">
              <label htmlFor="message">
                Message
                {formData.reason === "OTHER" && (
                  <span className="required-mark"> *</span>
                )}
              </label>

              <textarea
                id="message"
                name="message"
                rows="6"
                placeholder={
                  formData.reason === "OTHER"
                    ? "Please describe your issue..."
                    : "Tell us more about your issue (optional)..."
                }
                value={formData.message}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            {error && (
              <div className="contact-error">
                {error}
              </div>
            )}

            {success && (
              <div className="contact-success">
                {success}
              </div>
            )}

            <button
              type="submit"
              className="contact-submit"
              disabled={loading}
            >
              {loading ? "Sending..." : "Send Message"}
            </button>

          </form>

        </div>
      </div>
    </div>
  );
};

export default ContactUs;