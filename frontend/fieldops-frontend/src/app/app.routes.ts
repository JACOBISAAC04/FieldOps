import { Routes } from '@angular/router';
import { EquipmentList } from './features/equipment/equipment-list/equipment-list';
import { EquipmentForm } from './features/equipment/equipment-form/equipment-form';
import { EquipmentDetail } from './features/equipment/equipment-detail/equipment-detail';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'equipment',
    pathMatch: 'full'
  },
  {
    path: 'equipment',
    component: EquipmentList
  },
  {
    path: 'equipment/new',
    component: EquipmentForm
  },
  {
    path: 'equipment/:id/edit',
    component: EquipmentForm
  },
  {
    path: 'equipment/:id',
    component: EquipmentDetail
  }
];