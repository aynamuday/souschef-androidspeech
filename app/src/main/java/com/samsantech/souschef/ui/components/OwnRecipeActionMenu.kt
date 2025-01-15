package com.samsantech.souschef.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.utils.OwnRecipeAction
import com.samsantech.souschef.viewmodel.OwnRecipesViewModel

@Composable
fun OwnRecipeActionMenu(
    showRecipeActionMenu: Boolean,
    setShowRecipeActionMenu: (Boolean) -> Unit,
    recipeWithAction: Recipe?,
    setRecipeWithAction: (Recipe?) -> Unit,
    ownRecipesViewModel: OwnRecipesViewModel,
    onNavigateToCreateRecipeOne: () -> Unit,
    setLoading: (Boolean) -> Unit,
    onDeleted: (Boolean) -> Unit = {}
) {
    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }
    var successDelete by remember {
        mutableStateOf(false)
    }
    var error:String? by remember {
        mutableStateOf(null)
    }

    if (showRecipeActionMenu) {
        BottomActionMenuPopUp(
            options = hashMapOf("Edit" to R.drawable.pencil_icon, "Delete" to R.drawable.delete_icon),
            onClick = { key ->
                if (key == "Delete") {
                    showDeleteConfirmation = true
                    setShowRecipeActionMenu(false)
                } else if (key == "Edit") {
                    setShowRecipeActionMenu(false)
                    ownRecipesViewModel.action.value = OwnRecipeAction.EDIT
                    ownRecipesViewModel.recipe.value = recipeWithAction!!
                    ownRecipesViewModel.originalData.value = recipeWithAction
                    onNavigateToCreateRecipeOne()
                }
            },
            onOutsideClick = {
                setShowRecipeActionMenu(false)
                setRecipeWithAction(null)
            }
        )
    }

    if (showDeleteConfirmation) {
        ConfirmDialog(
            message = "Delete this recipe?",
            buttonOkayName = "Yes",
            onClickCancel = {
                showDeleteConfirmation = false
                setRecipeWithAction(null)
            },
            onClickOkay = {
                showDeleteConfirmation = false

                if (recipeWithAction != null) {
                    setLoading(true)

                    recipeWithAction.id?.let {
                        recipeWithAction.photosUrl.let { photosUrl ->
                            ownRecipesViewModel.deleteRecipe(it, photosUrl) { isSuccess, err ->
                                setLoading(false)

                                if (isSuccess) {
                                    onDeleted(true)
                                    successDelete = true
                                } else {
                                    error = err
                                }
                            }
                        }
                    }

                    setRecipeWithAction(null)
                }
            }
        )
    }

    if (successDelete) {
        Dialog(
            icon = "success",
            message = "Recipe successfully deleted!",
            onCloseClick = {
                successDelete = false
            }
        )
    }
}