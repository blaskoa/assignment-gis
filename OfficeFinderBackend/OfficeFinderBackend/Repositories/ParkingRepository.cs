using System.Collections.Generic;
using System.Linq;
using Dapper;
using GeoJSON.Net.Geometry;
using Newtonsoft.Json;
using OfficeFinderBackend.Models;

namespace OfficeFinderBackend.Repositories
{
   public class ParkingRepository : BaseRepository
   {
      private const string parkingSelect = 
         @" WITH max_distance AS 
            ( 
                        SELECT     Max(St_distance(parking.way, office.way)) 
                        FROM       PUBLIC.planet_osm_polygon AS parking 
                        CROSS JOIN PUBLIC.planet_osm_polygon AS office 
                        WHERE      parking.amenity='parking' 
                        AND        office.osm_id = @officeId 
                        AND        St_dwithin(parking.way, office.way, @distance) limit 1),
            min_distance AS 
            ( 
                        SELECT     min(st_distance(parking.way, office.way)) 
                        FROM       PUBLIC.planet_osm_polygon AS parking 
                        CROSS JOIN PUBLIC.planet_osm_polygon AS office 
                        WHERE      parking.amenity='parking' 
                        AND        office.osm_id = @officeId 
                        AND        st_dwithin(parking.way, office.way, @distance) limit 1) 
            SELECT     st_asgeojson(parking.way)::jsonb AS geojson, 
                        parking.NAME, 
                        parking.osm_id AS id, 
                        parking.access, 
                        st_distance(parking.way, office.way)                                                                     AS distance,
                        ((1/(max_distance.max - min_distance.min)) * (st_distance(parking.way, office.way) - min_distance.min) ) AS score
            FROM       PUBLIC.planet_osm_polygon                                                                                 AS parking
            CROSS JOIN PUBLIC.planet_osm_polygon                                                                                 AS office
            CROSS JOIN max_distance 
            CROSS JOIN min_distance 
            WHERE      parking.amenity='parking' 
            AND        office.osm_id = @officeId 
            AND        st_dwithin(parking.way, office.way, @distance) 
            ORDER BY   distance ASC limit 100";



      public IEnumerable<Parking> GetParking(ParkingRequest request)
      {
         var parameters = new
         {
            officeId = request.OfficeId,
            distance = request.Distance * 0.00001
         };

         IEnumerable<Parking> serializedResult = Connection.Query<Parking>(parkingSelect, parameters).ToList();

         return serializedResult.Select(FillParking);

      }


      private static Parking FillParking(Parking parking)
      {
         Polygon polygon = JsonConvert.DeserializeObject<Polygon>(parking.GeoJson);

         parking.GeoPointStrings = polygon.Coordinates.Select(x =>
         {
            return new GeoPointString
            {
               GeoPoints = x.Coordinates.Select(y => new GeoPoint
               {
                  Latitude = y.Latitude,
                  Longitude = y.Longitude
               })
            };
         });

         parking.Distance *= 100000;
         return parking;
      }

   }
}