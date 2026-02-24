let currentPage = 1; 

document.addEventListener('DOMContentLoaded', () => {
    const loadMoreBtn = document.getElementById('load-more');
    const spinner = document.getElementById('spinner');
    const container = document.getElementById('product-container');

    if (loadMoreBtn) {
        loadMoreBtn.addEventListener('click', function() {
            // Visual Feedback
            spinner.classList.remove('d-none');
            this.disabled = true;

            // --- CHANGE START: Define the fetch request and a minimum delay promise ---
            const fetchPromise = fetch(`/load-more-products?page=${currentPage}`)
                .then(response => {
                    if (!response.ok) throw new Error('Error al cargar productos');
                    return response.text();
                });

            // Set a minimum duration for the spinner (e.g., 800ms)
            const delayPromise = new Promise(resolve => setTimeout(resolve, 800));

            // Use Promise.all to wait for both the network response and the minimum delay
            Promise.all([fetchPromise, delayPromise])
                .then(([html]) => {
                    // --- CHANGE END ---
                    
                    container.insertAdjacentHTML('beforeend', html);

                    const noMoreMarker = container.querySelector('#no-more-marker');

                    if (noMoreMarker) {
                        this.innerText = "No more treasures found";
                        this.classList.replace('btn-load-more', 'btn-no-more');
                        this.disabled = true;

                        noMoreMarker.remove();
                    } else {
                        currentPage++;
                        this.disabled = false;
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert("No se pudieron cargar más productos.");
                    this.disabled = false;
                })
                .finally(() => {
                    spinner.classList.add('d-none');
                });
        });
    }
});