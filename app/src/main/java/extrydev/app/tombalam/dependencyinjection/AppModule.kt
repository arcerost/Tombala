package extrydev.app.tombalam.dependencyinjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import extrydev.app.tombalam.database.UserDao
import extrydev.app.tombalam.database.UserDatabase
import extrydev.app.tombalam.repository.TombalaRepository
import extrydev.app.tombalam.service.AuthInterceptor
import extrydev.app.tombalam.service.TokenManager
import extrydev.app.tombalam.service.TombalaApiService
import extrydev.app.tombalam.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context, tokenManager: TokenManager) = AuthInterceptor(context, tokenManager)

    @Singleton
    @Provides
    fun provideTokenManager(userDao: UserDao) = TokenManager(userDao)

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    @Named("Tombala")
    fun provideTombalaRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTombalaApiService(@Named("Tombala") tombalaRetrofit: Retrofit): TombalaApiService =
        tombalaRetrofit.create(TombalaApiService::class.java)

    @Singleton
    @Provides
    fun provideTombalaRepository(api: TombalaApiService, userDao: UserDao) = TombalaRepository(api, userDao)


    @Provides
    fun provideDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(context, UserDatabase::class.java, "UserInfo").build()
    }

    @Provides
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }
}