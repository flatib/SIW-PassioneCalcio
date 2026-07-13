import { useEffect, useId, useMemo, useRef, useState } from "react";

const initialFilters = {
  nome: "",
  categoria: "",
  prezzo: "",
};

function SearchBar({
  categorie = [],
  categorieError = "",
  categorieLoading = false,
  onSearch,
  onReset,
  loading = false,
}) {
  const formId = useId();
  const panelRef = useRef(null);
  const nameInputRef = useRef(null);
  const [filters, setFilters] = useState(initialFilters);
  const [highlighted, setHighlighted] = useState(false);

  const hasActiveFilters = useMemo(() => {
    return Boolean(filters.nome.trim() || filters.categoria || filters.prezzo);
  }, [filters]);

  function updateFilter(name, value) {
    setFilters((current) => ({ ...current, [name]: value }));
  }

  function buildPayload() {
    return {
      nome: filters.nome.trim(),
      categoria: filters.categoria === "" ? null : Number(filters.categoria),
      prezzo: filters.prezzo === "" ? null : Number(filters.prezzo),
    };
  }

  function handleSubmit(event) {
    event.preventDefault();
    onSearch(buildPayload());
  }

  function handleReset() {
    setFilters(initialFilters);
    onReset?.();
  }

  useEffect(() => {
    function openFromHash() {
      if (window.location.hash !== "#annunci-react-root") return;

      panelRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
      window.setTimeout(() => nameInputRef.current?.focus(), 250);
      setHighlighted(true);
      window.setTimeout(() => setHighlighted(false), 1400);
    }

    function openFromNavClick(event) {
      const link = event.target.closest?.('a[href="/#annunci-react-root"], a[href="/annunci#annunci-react-root"]');
      if (!link) return;

      window.setTimeout(openFromHash, 0);
    }

    openFromHash();
    window.addEventListener("hashchange", openFromHash);
    document.addEventListener("click", openFromNavClick);

    return () => {
      window.removeEventListener("hashchange", openFromHash);
      document.removeEventListener("click", openFromNavClick);
    };
  }, []);

  return (
    <section className="search-overlay" aria-label="Ricerca avanzata annunci">
      <form
        ref={panelRef}
        className={`search-panel${highlighted ? " search-panel--highlighted" : ""}`}
        onSubmit={handleSubmit}
      >
        <div className="search-panel__header">
          <div>
            <p className="search-panel__eyebrow">Marketplace AnnunciAmo</p>
            <h2>Trova l'annuncio giusto</h2>
          </div>

          <button
            className="search-panel__ghost-button"
            type="button"
            onClick={handleReset}
            disabled={loading || !hasActiveFilters}
          >
            Reset
          </button>
        </div>

        <div className="search-grid">
          <label className="search-field" htmlFor={`${formId}-nome`}>
            <span>Nome annuncio</span>
            <input
              ref={nameInputRef}
              id={`${formId}-nome`}
              type="search"
              placeholder="Es. iPhone, bici, scrivania"
              value={filters.nome}
              onChange={(event) => updateFilter("nome", event.target.value)}
            />
          </label>

          <label className="search-field" htmlFor={`${formId}-categoria`}>
            <span>Categoria</span>
            <select
              id={`${formId}-categoria`}
              value={filters.categoria}
              onChange={(event) => updateFilter("categoria", event.target.value)}
              disabled={categorieLoading}
            >
              <option value="">
                {categorieLoading ? "Caricamento categorie..." : "Tutte le categorie"}
              </option>
              {categorie.map((categoriaItem) => (
                <option key={categoriaItem.id} value={categoriaItem.id}>
                  {categoriaItem.nome || categoriaItem.name}
                </option>
              ))}
            </select>
          </label>

          <label className="search-field" htmlFor={`${formId}-prezzo`}>
            <span>Prezzo massimo</span>
            <input
              id={`${formId}-prezzo`}
              type="number"
              min="0"
              step="0.01"
              inputMode="decimal"
              placeholder="Es. 250"
              value={filters.prezzo}
              onChange={(event) => updateFilter("prezzo", event.target.value)}
            />
          </label>

          <button className="search-panel__submit" type="submit" disabled={loading}>
            {loading ? "Ricerca..." : "Cerca"}
          </button>
        </div>

        {categorieError && <p className="search-panel__error">{categorieError}</p>}
      </form>
    </section>
  );
}

export default SearchBar;
