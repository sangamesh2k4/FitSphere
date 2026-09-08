import "./foodSearchBox.css";

export default function FoodSearchBox({ query, setQuery, onSearch, loading }) {
    return (
        <section className="food-search-card">
            

            <form className="food-search-form" onSubmit={onSearch}>
                <input
                    type="text"
                    placeholder="Search chicken, rice, oats..."
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    className="search-input"
                />
                <button type="submit" className="search-button" disabled={loading}>
                    {loading ? "Searching..." : "Search"}
                </button>
            </form>
        </section>
    );
}