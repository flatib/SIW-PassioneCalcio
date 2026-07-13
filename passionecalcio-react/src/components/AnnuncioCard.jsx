function formatPrice(value) {
  if (value === null || value === undefined) return "Prezzo non indicato";

  return new Intl.NumberFormat("it-IT", {
    style: "currency",
    currency: "EUR",
  }).format(value);
}

function getAnnuncioImage(annuncio) {
  if (annuncio.immagine) {
    return `/annunci/${annuncio.id}/immagine`;
  }
  return (
    annuncio.immagineUrl ||
    annuncio.imageUrl ||
    annuncio.fotoUrl ||
    ""
  );
}

function AnnuncioCard({ annuncio }) {
  const titolo = annuncio.titolo || annuncio.nome || "Annuncio senza titolo";
  const imageUrl = getAnnuncioImage(annuncio);

  return (
    <a className="annuncio-card-react" href={`/annunci/${annuncio.id}`}>
      {imageUrl && (
        <img
          className="annuncio-card-react__image"
          src={imageUrl}
          alt={titolo}
          loading="lazy"
        />
      )}
      <div className="annuncio-card-react__body">
        <h3>{titolo}</h3>
        <strong className="annuncio-card-react__price">
          {formatPrice(annuncio.prezzo)}
        </strong>
      </div>
    </a>
  );
}

export default AnnuncioCard;
