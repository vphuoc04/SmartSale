// Api
import 'package:frontend/api/api.dart';

class UserCatalogueRepository {
  final Api api = Api();

  // Add user catalogue
  Future<dynamic> add(String name, int publish, { required String token }) {
    return api.post(
      'user_catalogue', {
        'name': name,
        'publish': publish
      }, 
      headers: {
        'Authorization': 'Bearer $token'
      }
    );
  }

  // Fetch user catalogue
  Future<dynamic> get({ required String token}) {
    return api.get(
      'user_catalogue',
      headers: {
        'Authorization': 'Bearer $token'
      }
    );
  }

  // Update user catalogue
  Future<dynamic> update(int id, String name, int publish, { required String token }) {
    return api.put(
      'user_catalogue/$id', {
        'name': name,
        'publish': publish
      },
      headers: {
        'Authorization': 'Bearer $token'
      }
    );
  }

  // Delete user catalgoue
  Future<dynamic> delete(int id, { required String token }) {
    return api.delete(
      'user_catalogue/$id',
      headers: {
        'Authorization': 'Bearer $token'
      }
    );
  }
}