import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { EngineerDetail } from './engineer-detail';
import { EngineerService } from '../../../core/services/engineer.service';

describe('EngineerDetail', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EngineerDetail],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '1'
              }
            }
          }
        },
        {
          provide: EngineerService,
          useValue: {
            getEngineerById: () => of({
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
    const fixture = TestBed.createComponent(EngineerDetail);
    expect(fixture.componentInstance).toBeTruthy();
  });
});