package com.samsantech.souschef.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@Composable
fun SaveRecipeDialog(showSaveRecipeDialog: MutableState<Boolean>, loading: MutableState<Boolean>,
                     action: OwnRecipeAction, changes:  MutableState<HashMap<String, Any>>, ownRecipesViewModel: OwnRecipesViewModel,
                     context: Context, success: MutableState<Boolean>, onSaveUpdate: () -> Unit = {}
) {
    ConfirmDialog(
        message = if (action == OwnRecipeAction.ADD) "Upload recipe?" else "Save changes?",
        buttonOkayName = "Continue",
        onClickCancel = { showSaveRecipeDialog.value = false }
    ) {
        showSaveRecipeDialog.value = false
        loading.value = true

        if (action == OwnRecipeAction.ADD) {
            ownRecipesViewModel.uploadRecipe { isSuccess, error ->
                loading.value = false

                if (!isSuccess && error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                } else {
                    success.value = true
                }
            }
        } else if (action == OwnRecipeAction.EDIT) {
            ownRecipesViewModel.updateRecipe(changes.value) { isSuccess, err ->
                loading.value = false

                if (!isSuccess && err != null) {
                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                } else {
                    onSaveUpdate()
                    success.value = true
                }

                changes.value = hashMapOf()
            }
        }
    }
}