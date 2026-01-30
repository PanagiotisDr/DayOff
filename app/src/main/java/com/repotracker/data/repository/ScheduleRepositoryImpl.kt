package com.repotracker.data.repository

import com.repotracker.data.local.WorkScheduleDao
import com.repotracker.data.local.WorkScheduleEntity
import com.repotracker.domain.model.WorkSchedule
import com.repotracker.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Υλοποίηση του ScheduleRepository.
 * Χρησιμοποιεί Room για persistence.
 */
@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val dao: WorkScheduleDao
) : ScheduleRepository {
    
    override fun getSchedule(): Flow<WorkSchedule?> {
        return dao.getSchedule().map { entity ->
            entity?.toDomainModel()
        }
    }
    
    override suspend fun saveSchedule(schedule: WorkSchedule) {
        dao.insertSchedule(WorkScheduleEntity.fromDomainModel(schedule))
    }
    
    override suspend fun deleteSchedule() {
        dao.deleteAll()
    }
}
