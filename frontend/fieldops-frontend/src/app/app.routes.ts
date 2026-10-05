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
import { Login } from './features/auth/login/login';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';
export const routes: Routes = [
  { path: 'login', component: Login },

  { path: '', redirectTo: 'login', pathMatch: 'full' },

  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard]
  },

  {
    path: 'equipment',
    component: EquipmentList,
    canActivate: [authGuard]
  },

  {
    path: 'equipment/new',
    component: EquipmentForm,
    canActivate: [authGuard]
  },

  {
    path: 'equipment/:id/edit',
    component: EquipmentForm,
    canActivate: [authGuard]
  },

  {
    path: 'equipment/:id',
    component: EquipmentDetail,
    canActivate: [authGuard]
  },

  {
  path: 'engineers',
  component: EngineerList,
  canActivate: [
    authGuard,
    roleGuard(['ADMIN', 'ENGINEER', 'FIELD_ENGINEER'])
  ]
},
{
  path: 'engineers/new',
  component: EngineerForm,
  canActivate: [
    authGuard,
    roleGuard(['ADMIN'])
  ]
},
{
  path: 'engineers/:id/edit',
  component: EngineerForm,
  canActivate: [
    authGuard,
    roleGuard(['ADMIN'])
  ]
},
{
  path: 'engineers/:id',
  component: EngineerDetail,
  canActivate: [
    authGuard,
    roleGuard(['ADMIN', 'ENGINEER', 'FIELD_ENGINEER'])
  ]
},

  {
    path: 'work-orders',
    component: WorkOrderList,
    canActivate: [authGuard]
  },

  {
    path: 'work-orders/new',
    component: WorkOrderForm,
    canActivate: [authGuard]
  },

  {
    path: 'work-orders/:id',
    component: WorkOrderDetail,
    canActivate: [authGuard]
  }
];