'use strict'

//Folder
const FOLDER_RENAME_MODAL = "folderRenameModal";
const FOLDER_CURRENT_NAME_DATA = "data-folder-current-name";
const FIELD_CURRENT_NAME_ID = "folderCurrentName";

//File
const FILE_RENAME_MODAL = "fileRenameModal";
const FILE_CURRENT_NAME_ID = "fileCurrentName";
const FILE_EXTENSION_ID = "fileExtension";
const FILE_CURRENT_NAME_DATA = "data-file-current-name";
const FILE_EXTENSION_DATA = "data-file-extension";

document.addEventListener('DOMContentLoaded', () => {
    const folderRenameModal = document.getElementById(FOLDER_RENAME_MODAL);
    const fileRenameModal = document.getElementById(FILE_RENAME_MODAL);

    if (folderRenameModal) {
        folderRenameModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            if (button) {
                const currentNameField = document.getElementById(FIELD_CURRENT_NAME_ID);

                if (currentNameField) {
                    const folderCurrentNameInput = button.getAttribute(FOLDER_CURRENT_NAME_DATA);
                    currentNameField.value = folderCurrentNameInput;
                }
            }
        });
    }

    if (fileRenameModal) {
        fileRenameModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            if (button) {
                const fileCurrentNameInput = document.getElementById(FILE_CURRENT_NAME_ID);
                const fileExtensionInput = document.getElementById(FILE_EXTENSION_ID);

                if (fileCurrentNameInput && fileExtensionInput) {
                    const fileCurrentNameDataAttr = button.getAttribute(FILE_CURRENT_NAME_DATA);
                    const fileExtensionDataAttr = button.getAttribute(FILE_EXTENSION_DATA);

                    fileCurrentNameInput.value = fileCurrentNameDataAttr;
                    fileExtensionInput.value = fileExtensionDataAttr;
                }
            }
        });
    }
})

