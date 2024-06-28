'use strict'

const RENAME_MODAL = "folderRenameModal";

const DATA_PATH = "data-folder-path";
const DATA_NAME = "data-folder-name";

const FIELD_UPDATED_NAME = "updatedName";
const FIELD_CURRENT_NAME = "currentName";
const FIELD_PATH = "path";


document.addEventListener('DOMContentLoaded', () => {
    const folderRenameModal = document.getElementById(RENAME_MODAL);

    if (folderRenameModal) {
        folderRenameModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            if (button) {
                const folderName = button.getAttribute(DATA_NAME);
                const folderPath = button.getAttribute(DATA_PATH);

                setModalFields(folderName, folderPath)
            }
        });
    }
})

//Setting input forms in modal view with data-attributes
function setModalFields(folderName, folderPath) {
    const updatedNameField = document.getElementById(FIELD_UPDATED_NAME);
    const currentNameField = document.getElementById(FIELD_CURRENT_NAME);
    const pathField = document.getElementById(FIELD_PATH);

    if (updatedNameField && currentNameField && pathField) {
        updatedNameField.value = folderName;
        currentNameField.value = folderName;
        pathField.value = folderPath
    }
}
