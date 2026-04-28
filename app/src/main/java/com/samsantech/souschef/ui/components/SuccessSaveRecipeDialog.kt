package com.samsantech.souschef.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@Composable
fun SuccessSaveRecipeDialog(action: OwnRecipeAction, closeCreateRecipe: () -> Unit,
                            onNavigateToProfile: () -> Unit = {}, ownRecipesViewModel: OwnRecipesViewModel, success: MutableState<Boolean>) {
    Dialog(
        icon = "success",
        message = if (action == OwnRecipeAction.EDIT) "Recipe updated successfully!" else "Recipe uploaded successfully!",
        subMessage = null,
        onCloseClick = {
            if (action == OwnRecipeAction.EDIT) closeCreateRecipe() else onNavigateToProfile()
            ownRecipesViewModel.action.value = OwnRecipeAction.ADD
            success.value = false
        }
    )
}