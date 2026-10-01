import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { EngineerForm } from './engineer-form';
import { EngineerService } from '../../../core/services/engineer.service';

describe('EngineerForm', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EngineerForm],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => null
              }
            }
          }
        },
        {
          provide: EngineerService,
          useValue: {
            getAvailableEngineers: () => of([
              {
                id: 1,
                name: 'Jacob Isaac',
                email: 'jacob@fieldops.local'
              }
            ]),
            getEngineerById: () => of({
              id: 1,
              userId: 1,
              name: 'Jacob Isaac',
              email: 'jacob@fieldops.local',
              specialization: 'Mechanical Maintenance',
              location: 'Kerala',
              availability: 'AVAILABLE'
            }),
            createEngineer: () => of({
              id: 1,
              userId: 1,
              name: 'Jacob Isaac',
              email: 'jacob@fieldops.local',
              specialization: 'Mechanical Maintenance',
              location: 'Kerala',
              availability: 'AVAILABLE'
            }),
            updateEngineer: () => of({
              id: 1,
              userId: 1,
              name: 'Jacob Isaac',
              email: 'jacob@fieldops.local',
              specialization: 'Mechanical Maintenance',
              location: 'Kerala',
              availability: 'AVAILABLE'
            })
          }
        }
      ]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(EngineerForm);
    expect(fixture.componentInstance).toBeTruthy();
  });
});