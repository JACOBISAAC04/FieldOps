import { Routes } from '@angular/router';
import { EquipmentList } from './features/equipment/equipment-list/equipment-list';
import { EquipmentForm } from './features/equipment/equipment-form/equipment-form';
import { EquipmentDetail } from './features/equipment/equipment-detail/equipment-detail';
import { WorkOrderList } from './features/work-orders/work-order-list/work-order-list';
import { WorkOrderForm } from './features/work-orders/work-order-form/work-order-form';
import { WorkOrderDetail } from './features/work-orders/work-order-detail/work-order-detail';
import { EngineerList } from './features/engineers/engineer-list/engineer-list';
import { EngineerDetail } from './features/engineers/engineer-detail/engineer-detail';
import { EngineerForm } from './features/engineers/engineer-form/engineer-form';
import { Dashboard } from './features/dashboard/dashboard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    component: Dashboard
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
  },
  {
    path: 'engineers',
    component: EngineerList
  },
  {
    path: 'engineers/new',
    component: EngineerForm
  },
  {
    path: 'engineers/:id/edit',
    component: EngineerForm
  },
  {
    path: 'engineers/:id',
    component: EngineerDetail
  },
  {
    path: 'work-orders',
    component: WorkOrderList
  },
  {
    path: 'work-orders/new',
    component: WorkOrderForm
  },
  {
    path: 'work-orders/:id',
    component: WorkOrderDetail
  }
];