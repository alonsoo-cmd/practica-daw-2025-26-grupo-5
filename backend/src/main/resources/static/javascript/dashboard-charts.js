document.addEventListener("DOMContentLoaded", function() {

    const rawDataElement = document.getElementById('chart-data');
    let chartLabels = [];
    let chartValues = [];

    if (rawDataElement) {
        try {
            const chartData = JSON.parse(rawDataElement.textContent);
            chartLabels = chartData.labels; 
            chartValues = chartData.values;
        } catch (error) {
            console.error("Error at the chart data parsing:", error);
        }
    }

    if (chartLabels && chartLabels.length > 0) {
        const ctx = document.getElementById('salesByCategoryChart').getContext('2d');

        const brandPalette = [
            '#2f6ced',
            '#111827',
            '#a5b4fc',
            '#c5ccdc',
            '#818cf8' 
        ];

        Chart.defaults.font.family = "'Inter', sans-serif";
        Chart.defaults.font.size = 13;
        Chart.defaults.color = '#6b7280'; 
        
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: chartLabels,
                datasets: [{
                    label: 'Products Sold',
                    data: chartValues,
                    backgroundColor: brandPalette,
                    borderWidth: 2,          
                    borderColor: '#ffffff',  
                    hoverOffset: 8,          
                    spacing: 3 
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true,
                            pointStyle: 'circle',
                            font: {
                                size: 14,
                                weight: '500'
                            },
                            color: '#1f2937'
                        }
                    },
                    tooltip: {
                        backgroundColor: '#111827',
                        padding: 12,
                        cornerRadius: 8,  
                        titleFont: {
                            size: 14,
                            weight: '700',
                            family: "'Inter', sans-serif"
                        },
                        bodyFont: {
                            size: 13,
                            family: "'Inter', sans-serif"
                        },
                        displayColors: true,
                        boxWidth: 8,
                        boxHeight: 8,
                        boxPadding: 6
                    }
                }
            }
        });
    } else {
        const chartCanvas = document.getElementById('salesByCategoryChart');
        if (chartCanvas) {
            chartCanvas.parentElement.innerHTML = 
                '<div class="d-flex align-items-center justify-content-center h-100">' +
                '  <p class="text-muted text-center small">You have no sales data to display yet.</p>' +
                '</div>';
        }
    }
});