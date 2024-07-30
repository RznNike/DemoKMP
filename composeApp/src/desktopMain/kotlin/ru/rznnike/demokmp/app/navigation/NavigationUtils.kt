package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun getFlowNavigator() = FlowNavigator(LocalNavigator.currentOrThrow.parent!!)

@Composable
fun getScreenNavigator() = ScreenNavigator(LocalNavigator.currentOrThrow)