import { Modal } from 'bootstrap';
export function showServiceDetails(service) {

  if (service) {

    const modalContent = `
            <div class="modal fade" id="serviceModal" tabindex="-1" aria-labelledby="serviceModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="serviceModalLabel">${service.name}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <img src="data:image/jpeg;base64,${service.imageBase64}" alt="${service.name}" class="img-fluid mb-3">
                            <p><strong>Описание:</strong> ${service.description || 'Описание отсутствует'}</p>
                            <p><strong>Стоимость:</strong> ${service.workPrice} руб.</p>
                            <p><strong>Дополнительная информация:</strong> ${service.details || 'Нет дополнительной информации'}</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                        </div>
                    </div>
                </div>
            </div>
        `;


    document.body.insertAdjacentHTML('beforeend', modalContent);


    const modal = new Modal(document.getElementById('serviceModal'));
    modal.show();

    document.getElementById('serviceModal').addEventListener('hidden.bs.modal', () => {
      document.getElementById('serviceModal').remove();
    });
  } else {
    alert('Услуга не найдена!');
  }
}
