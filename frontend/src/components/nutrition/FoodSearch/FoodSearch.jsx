import "../FoodSearch/FoodSearch.css";

export default function FoodSearch({
    search,
    setSearch,
    loading,
}) {

    return (
        <div className="search-box">

            <input
                type="text"
                placeholder="Search foods (min. 3 characters)..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
            />
            <p className="search-helper">
    Enter at least 3 characters
</p>

            {loading && (
                <span className="search-loading">
                    Searching...
                </span>
            )}

        </div>
    );

}