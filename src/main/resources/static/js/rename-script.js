'use strict'

//Folder
const FOLDER_RENAME_MODAL = "folderRenameModal";
const FOLDER_CURRENT_NAME_DATA = "data-folder-current-name";
const FOLDER_PATH_DATA = "data-folder-path";
const FOLDER_PATH_ID = "folderPath";
const FOLDER_CURRENT_NAME_ID = "folderCurrentName";

//File
const FILE_RENAME_MODAL = "fileRenameModal";
const FILE_CURRENT_NAME_ID = "fileCurrentName";
const FILE_EXTENSION_ID = "fileExtension";
const FILE_PATH_ID = "filePath";
const FILE_PATH_DATA = "data-folder-path";
const FILE_CURRENT_NAME_DATA = "data-file-current-name";
const FILE_EXTENSION_DATA = "data-file-extension";

document.addEventListener('DOMContentLoaded', () => {
    const folderRenameModal = document.getElementById(FOLDER_RENAME_MODAL);
    if (folderRenameModal) {

        folderRenameModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            if (button) {
                const folderCurrentNameInput = document.getElementById(FOLDER_CURRENT_NAME_ID);
                const folderPathInput = document.getElementById(FOLDER_PATH_ID);
                if (folderCurrentNameInput && folderPathInput) {
                    const folderCurrentNameData = button.getAttribute(FOLDER_CURRENT_NAME_DATA);
                    const folderPathData = button.getAttribute(FOLDER_PATH_DATA);

                    folderCurrentNameInput.value = folderCurrentNameData;
                    folderPathInput.value = folderPathData;
                }
            }
        });
    }

    const fileRenameModal = document.getElementById(FILE_RENAME_MODAL);
    if (fileRenameModal) {
        fileRenameModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            if (button) {
                const fileCurrentNameInput = document.getElementById(FILE_CURRENT_NAME_ID);
                const fileExtensionInput = document.getElementById(FILE_EXTENSION_ID);
                const filePathInput = document.getElementById(FILE_PATH_ID);

                if (fileCurrentNameInput && fileExtensionInput) {
                    const fileCurrentNameDataAttr = button.getAttribute(FILE_CURRENT_NAME_DATA);
                    const fileExtensionDataAttr = button.getAttribute(FILE_EXTENSION_DATA);
                    const filePathData = button.getAttribute(FILE_PATH_DATA);

                    fileCurrentNameInput.value = fileCurrentNameDataAttr;
                    fileExtensionInput.value = fileExtensionDataAttr;
                    filePathInput.value = filePathData;
                }
            }
        });
    }
})

