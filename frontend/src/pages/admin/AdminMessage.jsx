import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import adminService from "../../services/adminService";
import CustomSelect from "../../components/common/CustomSelect";
import "../../css/admin/AdminMessage.css";
import LoadingSkeleton from "../../components/skeleton/LoadingSkeleton/LoadingSkeleton";

function AdminMessage() {
    const navigate = useNavigate();
    const { id } = useParams();

    const [message, setMessage] = useState(null);
    const [reply, setReply] = useState("");
    const [loading, setLoading] = useState(true);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        const fetchMessage = async () => {
            try {
                setLoading(true);
                const data = await adminService.getContactMessageById(id);
                setMessage(data);
            } catch (error) {
                console.error("Failed to load message:", error);
                navigate("/admin/inbox");
            } finally {
                setLoading(false);
            }
        };

        fetchMessage();
    }, [id, navigate]);

    const handleStatusChange = (event) => {
        const status = event.target.value;
        
        async function updateStatus() {
            try {
                await adminService.updateContactMessageStatus(id, status);
                setMessage((prev) => ({
                    ...prev,
                    status
                }));
            } catch (error) {
                console.error("Failed to update message status:", error);
            }
        }

        updateStatus();
    };

    const handleReply = async () => {
        if (!reply.trim()) {
            return;
        }

        try {
            await adminService.replyToContactMessage(id, reply);
            setReply("");

            if (message.status === "OPEN") {
                await adminService.updateContactMessageStatus(id, "IN_PROGRESS");
                setMessage((prev) => ({
                    ...prev,
                    status: "IN_PROGRESS"
                }));
            }
        } catch (error) {
            console.error("Failed to send reply:", error);
        }
    };

    const confirmDelete = async () => {
        try {
            await adminService.deleteContactMessage(id);
            navigate("/admin/inbox");
        } catch (error) {
            console.error("Failed to delete message:", error);
        } finally {
            setShowDeleteModal(false);
        }
    };

    if (loading) {
        return <LoadingSkeleton />;
    }

    if (!message) {
        return null;
    }

    const statusOptions = [
        { value: "OPEN", label: "Open" },
        { value: "IN_PROGRESS", label: "In Progress" },
        { value: "RESOLVED", label: "Resolved" }
    ];

    return (
        <section className="admin-message-page">
            <div className="admin-message-top-nav">
                <span className="admin-message-super-label">MESSAGE DETAIL</span>
                <button
                    type="button"
                    className="btn-back-inbox"
                    onClick={() => navigate("/admin/inbox")}
                >
                    &larr; Back to Inbox
                </button>
            </div>

            <article className="admin-message-card">
                <header className="msg-header-row">
                    <div className="msg-title-group">
                        <h2>{message.email}</h2>
                        <div className="msg-meta-group">
                            <span className="msg-reason">
                                {message.reason ? message.reason.replace(/_/g, " ") : "OTHER"}
                            </span>
                            <span className="msg-dot">&middot;</span>
                            <span className="msg-date">
                                {new Date(message.createdAt).toLocaleString('en-US', {
                                    month: 'short',
                                    day: 'numeric',
                                    year: 'numeric',
                                    hour: 'numeric',
                                    minute: '2-digit'
                                })}
                            </span>
                        </div>
                    </div>

                    <span className={`msg-status-badge badge-${message.status.toLowerCase()}`}>
                        {message.status === "IN_PROGRESS"
                            ? "In Progress"
                            : message.status === "OPEN"
                            ? "Open"
                            : "Resolved"}
                    </span>
                </header>

                <div className="msg-content-box">
                    <p>{message.message}</p>
                </div>

                <hr className="msg-divider" />

                <div className="msg-form-group">
                    <label htmlFor="message-status" className="msg-form-label">
                        STATUS
                    </label>
                    <CustomSelect
                        name="status"
                        options={statusOptions}
                        value={message.status}
                        onChange={handleStatusChange}
                        placeholder="Select Status"
                    />
                </div>

                <div className="msg-form-group">
                    <label htmlFor="message-reply" className="msg-form-label">
                        REPLY
                    </label>
                    <textarea
                        id="message-reply"
                        className="msg-textarea"
                        value={reply}
                        onChange={(event) => setReply(event.target.value)}
                        placeholder="Type your reply..."
                    />
                </div>

                <footer className="msg-actions-footer">
                    <button
                        type="button"
                        className="btn-delete"
                        onClick={() => setShowDeleteModal(true)}
                    >
                        Delete Message
                    </button>

                    <button
                        type="button"
                        className="btn-reply"
                        onClick={handleReply}
                    >
                        Send Reply
                    </button>
                </footer>
            </article>

            {showDeleteModal && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal-container">
                        <h3>Confirm Deletion</h3>
                        <p>Are you sure you want to delete this message? This action cannot be undone.</p>
                        <div className="admin-modal-actions">
                            <button
                                type="button"
                                className="admin-modal-btn-cancel"
                                onClick={() => setShowDeleteModal(false)}
                            >
                                Cancel
                            </button>
                            <button
                                type="button"
                                className="admin-modal-btn-confirm"
                                onClick={confirmDelete}
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </section>
    );
}

export default AdminMessage;