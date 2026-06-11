package com.example.shoppinglist.di

import com.example.shoppinglist.domain.ShoppingRepository
import com.example.shoppinglist.domain.usecase.GetShoppingListUseCase
import com.example.shoppinglist.domain.usecase.ManageItemUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetShoppingListUseCase(repository: ShoppingRepository): GetShoppingListUseCase {
        return GetShoppingListUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideManageItemUseCase(repository: ShoppingRepository): ManageItemUseCase {
        return ManageItemUseCase(repository)
    }
}
