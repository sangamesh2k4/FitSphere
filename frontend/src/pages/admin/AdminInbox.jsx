import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import adminService from "../../services/adminService";
import CustomSelect from "../../components/common/CustomSelect";
import "../../css/admin/AdminInbox.css";

function AdminInbox() {
    const navigate = useNavigate();
    const loadMoreRef = useRef(null);

    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadingMore, setLoadingMore] = useState(false);

    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    const [status, setStatus] = useState("");
    const [sortDirection, setSortDirection] = useState("desc");

    const fetchMessages = async (pageNumber, append = false) => {
        try {
            if (append) {
                setLoadingMore(true);
            } else {
                setLoading(true);
            }

            const data = await adminService.getContactMessages({
                page: pageNumber,
                status,
                sortBy: "createdAt",
                direction: sortDirection
            });

            setMessages((prev) =>
                append ? [...prev, ...data.content] : data.content
            );

            setHasMore(!data.last);
        } catch (error) {
            console.error("Failed to load inbox:", error);
        } finally {
            setLoading(false);
            setLoadingMore(false);
        }
    };

    useEffect(() => {
        setPage(0);
        setHasMore(true);
        fetchMessages(0, false);
    }, [status, sortDirection]);

    useEffect(() => {
        if (page === 0) {
            return;
        }
        fetchMessages(page, true);
    }, [page]);

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                if (
                    entries[0].isIntersecting &&
                    hasMore &&
                    !loading &&
                    !loadingMore
                ) {
                    setPage((prev) => prev + 1);
                }
            },
            { threshold: 0.1 }
        );

        const element = loadMoreRef.current;
        if (element) {
            observer.observe(element);
        }

        return () => {
            if (element) {
                observer.unobserve(element);
            }
        };
    }, [hasMore, loading, loadingMore]);

    const handleStatusFilter = (event) => {
        setStatus(event.target.value);
    };

    const handleSortChange = (event) => {
        setSortDirection(event.target.value);
    };

    const statusOptions = [
        { value: "", label: "All Status" },
        { value: "OPEN", label: "Open" },
        { value: "IN_PROGRESS", label: "In Progress" },
        { value: "RESOLVED", label: "Resolved" }
    ];

    const sortOptions = [
        { value: "desc", label: "Newest First" },
        { value: "asc", label: "Oldest First" }
    ];

    if (loading && page === 0) {
        return <p className="admin-inbox-loading">Loading inbox...</p>;
    }

    return (
        <section className="admin-inbox-page">
            <div className="admin-inbox-header">
                <h1>INBOX</h1>
                <p className="admin-inbox-subtitle">
                    Messages sent from the public contact form.
                </p>
            </div>

            <div className="admin-inbox-filters-card">
                <CustomSelect
                    name="status"
                    options={statusOptions}
                    value={status}
                    onChange={handleStatusFilter}
                    placeholder="All Status"
                />

                <CustomSelect
                    name="sortDirection"
                    options={sortOptions}
                    value={sortDirection}
                    onChange={handleSortChange}
                    placeholder="Sort Order"
                />
            </div>

            <div className="admin-inbox-list">
                {messages.map((message) => (
                    <article key={message.id} className="admin-inbox-card">
                        <div className="admin-inbox-main">
                            <h3>{message.email}</h3>
                            <span className="admin-inbox-reason">
                                {message.reason ? message.reason.replace(/_/g, " ") : "OTHER"}
                            </span>
                        </div>

                        <div className="admin-inbox-meta">
                            <span
                                className={`admin-inbox-status status-${message.status.toLowerCase()}`}
                            >
                                {message.status === "IN_PROGRESS"
                                    ? "In Progress"
                                    : message.status === "OPEN"
                                    ? "Open"
                                    : "Resolved"}
                            </span>

                            <span className="admin-inbox-date">
                                {new Date(message.createdAt).toLocaleDateString('en-US', {
                                    month: 'short',
                                    day: 'numeric',
                                    year: 'numeric'
                                })}
                            </span>

                            <button
                                type="button"
                                className={`admin-inbox-action-btn ${
                                    message.status === "RESOLVED" ? "btn-outline" : "btn-solid"
                                }`}
                                onClick={() => navigate(`/admin/inbox/${message.id}`)}
                            >
                                Open
                            </button>
                        </div>
                    </article>
                ))}
            </div>

            <div ref={loadMoreRef} className="admin-inbox-load-more">
                {loadingMore && <p>Loading more...</p>}
                {!hasMore && messages.length > 0 && <p>No more messages.</p>}
            </div>
        </section>
    );
}

export default AdminInbox;